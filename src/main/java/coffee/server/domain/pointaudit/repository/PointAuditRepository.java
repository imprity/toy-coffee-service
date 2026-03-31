package coffee.server.domain.pointaudit.repository;

import coffee.server.domain.pointaudit.entity.PointAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointAuditRepository extends JpaRepository<PointAudit, Long> {}
