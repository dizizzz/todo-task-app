package com.todotask.backend.task.dao.repository;

import com.todotask.backend.task.dao.model.Task;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t WHERE t.ownerId = :userId OR :userId MEMBER OF t.collaboratorIds")
    Page<Task> findByOwnerOrCollaborator(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE :userId MEMBER OF t.collaboratorIds")
    List<Task> findByCollaborator(@Param("userId") Long userId);

    void deleteByOwnerId(Long ownerId);
}
