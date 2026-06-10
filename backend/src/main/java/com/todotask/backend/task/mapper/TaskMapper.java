package com.todotask.backend.task.mapper;

import com.todotask.backend.task.dao.dto.TaskResponse;
import com.todotask.backend.task.dao.model.Task;
import com.todotask.backend.user.mapper.UserMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface TaskMapper {
    TaskResponse toResponse(Task task);
}
