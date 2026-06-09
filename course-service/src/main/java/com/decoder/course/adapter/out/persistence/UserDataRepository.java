package com.decoder.course.adapter.out.persistence;
import com.decoder.course.domain.model.UserDataModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface UserDataRepository extends JpaRepository<UserDataModel, UUID> {}
