# Toy-Coffee-Shop

간단한 장난감 커피 판매 시스템

# 프로젝트 개요

- 프로젝트 목적: Spring공부를 위한 간단한 커피 판매 시스템

# 기술 스택
- Language: Java 17
- IDE: IntelliJ IDEA

# 빌드

빌드를 위해서는 JDK 17이 필요합니다.

이 명령어를 입력해 주세요
```
gradlew build -x test
```

# 실행

실행을 위해서는 MariaDB가 필요합니다. 저는 현재 AWS가 지원하는 MariaDB중 최신 버전 11.8.6을 타겟으로 하였지만 버전이 살짝 달라도 상관이 없을 것입니다. (아마도)

# 테스트

테스트는 MariaDB가 돌고 있다는 것을 전제로 작성되었습니다. 이를 위해 docker-compose-test.yml를 작성하였고 또 test-all.bat이라는 간단한 batch파일로 이를 자동화 하였으니 참고해 주세요.

# 구현시 고려사항

## 멱등성

멱등성을 지키기 위해서 `idempotency_caches` 라는 테이블을 만들었습니다.
그리고 포인트 충전, 포인트 설정, 커피 구매, 두번 요청이 갔을 경우에도 한번만 처리해야 할 경우 client한테 UUID로 멱등키를 만들어서 주게끔 만들었습니다.

그래서 고객이 요청을 하고 요청처리가 성공했을 시 테이블에 저장하고 다시 그 요청이 다시 들어오더라도 테이블에 이미 처리한 요청이 있다면 기억한 반환값을 돌려주도록 만들었습니다.

그리고 이를 구현하기 위해 upsert를 활용했습니다.
``` java
public interface IdempotencyCacheRepository extends JpaRepository<IdempotencyCache, Long> {
    Optional<IdempotencyCache> findByIdempotencyCacheKey(UUID idempotencyCacheKey);

    @Modifying
    @NativeQuery(
            """
        INSERT INTO idempotency_caches (
            idempotency_cache_key,
            idempotency_cache_value,
            created_at,
            modified_at
        ) VALUES (
            :idempotencyCacheKey,
            :idempotencyCacheValue,
            :createdAt,
            :createdAt
        ) ON DUPLICATE KEY UPDATE modified_at=:createdAt;
    """)
    Long putCacheImpl(
            @Param("idempotencyCacheKey") UUID idempotencyCacheKey,
            @Param("idempotencyCacheValue") String idempotencyCacheValue,
            @Param("createdAt") Instant createdAt);

    default boolean putCache(UUID idempotencyCacheKey, String idempotencyCacheValue, Instant createdAt) {
        Long res = putCacheImpl(idempotencyCacheKey, idempotencyCacheValue, createdAt);

        if (res == 1) {
            return true;
        } else {
            return false;
        }
    }
}
```

upsert를 활용한 이유는 SELECT ... FOR UPDATE로 조회한 뒤 별도로 INSERT하는 방식의 race condition과 데드락을 원천적으로 방지하기 위해서입니다. 아직 존재하지 않는 row에 대해서는 비관적 락을 걸 수 없기 때문에, 두 트랜잭션이 동시에 "row 없음"을 확인한 뒤 동시에 INSERT를 시도하면 gap lock 충돌로 인한 데드락이 발생할 수 있습니다.

물론 unique key constraint만으로도 중복 삽입 자체는 방지됩니다. 하지만 unique key 에러에만 의존할 경우, 해당 에러가 idempotency_cache_key 충돌로 인한 것인지 다른 unique key 충돌로 인한 것인지 구분이 어려워집니다. 따라서 upsert의 반환값(영향받은 row 수)을 기반으로 중복 여부를 판단하고, 중복일 경우 DuplicateCacheKeyException을 던지도록 구현했습니다.

이에 대해서는 이 블로그에 더 잘 정리되어 있습니다. https://m.blog.naver.com/cmw1728/222590578274

## TOP3 Coffee 조회

Top3 coffee 조회는 coffee 테이블에 `coffee_order_count`라는 column을 추가하여 구현 하였습니다. 커피를 주문할 때 마다 이 숫자를 올리고 client가 가장 많이 팔린 커피 top3를 달라고 하면 이 값을 이용해 정렬한뒤 돌려주도록 하였습니다.

정렬을 DB에서 하지 않고 서버에서 한 이유는 커피의 메뉴는 별로 없을 것이라 생각했기 때문입니다.

그리고 일주일 간의 top3가 기준이기 때문에  일주일 마다 Scheduler를 활용해 커피 순위를 초기화 하였습니다.

## 커피 주문데이터 전송

커피 데이터 주문 전송은 `@Async`를 활용했습니다.

이를 통해 네트워크에 문제가 생기더라도 실제로 커피가 주문이 막히는 일이 없도록 방지했습니다.

또 coffeeOrderLoggingTaskExecutor에 setRejectedExecutionHandler는 LoggingDiscardPolicy라는 직접 만든 policy를 사용합니다.

이미 만들어진 policy를 쓰지 않고 직접 만든 이유는 제생각에 데이터 전송은 커피 주문에 지장을 줄만큼 중요하지는 않지만 그래도 그냥 DiscardPolicy를 쓰기 보다는 log라도 남겨 놓고 싶었기 때문입니다.

# API 명세서

## GET /api/coffees/{coffeeId}

커피 하나의 정보를 가져옵니다.

## GET /api/coffees

커피 전체 정보를 가져옵니다.

## GET /api/coffees/top##selling

인기있는 커피 (주문 수량이 많은 커피) top 3를 가졍옵니다

## POST /api/coffee##orders

커피를 주문합니다.

## GET /api/points

고객 포인트 정보를 조회합니다.

## POST /api/points/set

고객 포인트를 설정합니다.

## POST /api/points/add

고객 포인트를 충전합니다.

# ERD

![erd](readme/erd.png)

# Git Convention

- FEAT:     feature 추가
- FIX:      버그 수정
- REFACTOR: refactoring
- MISC:     기타

