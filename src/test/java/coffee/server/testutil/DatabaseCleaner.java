package coffee.server.testutil;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleaner {
    @Autowired
    JdbcTemplate template;

    public void deleteTables(Class<?>... entityClasses) {
        JdbcClient client = JdbcClient.create(template);
        client.sql("SET FOREIGN_KEY_CHECKS=0").update();

        for (Class<?> entityClass : entityClasses) {
            String tableName = convertCamelCaseToSnake(entityClass.getSimpleName());

            // @Entity 가 있는지 확인
            {
                Entity entity = entityClass.getAnnotation(Entity.class);
                if (entity == null) {
                    throw new RuntimeException(String.format("%s는 entity class가 아닙니다.", entityClass.getSimpleName()));
                }
            }

            // @Table이 있는지 확인
            {
                Table table = entityClass.getAnnotation(Table.class);
                if (table != null) {
                    String annotationTableName = table.name();
                    if (!annotationTableName.isBlank()) {
                        tableName = annotationTableName;
                    }
                }
            }
            client.sql("TRUNCATE TABLE " + tableName).update();
        }

        client.sql("SET FOREIGN_KEY_CHECKS=1").update();
    }

    public static String convertCamelCaseToSnake(String input) {
        StringBuilder result = new StringBuilder();
        boolean isFirstChar = true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isUpperCase(c)) {
                if (isFirstChar) {
                    result.append(Character.toLowerCase(c));
                } else {
                    result.append("_").append(Character.toLowerCase(c));
                }
            } else {
                result.append(c);
            }

            isFirstChar = false;
        }
        return result.toString();
    }
}
