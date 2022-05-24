package vn.cloud.gcphello.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.cloud.gcphello.entity.Record;

public interface RecordRepository extends JpaRepository<Record, Long> {
}
