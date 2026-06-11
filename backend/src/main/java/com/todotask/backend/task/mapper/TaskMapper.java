package com.todotask.backend.task.mapper;

import com.todotask.backend.task.dao.dto.TaskResponse;
import com.todotask.backend.task.dao.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "collaborators", ignore = true)
    TaskResponse toResponse(Task task);
}
