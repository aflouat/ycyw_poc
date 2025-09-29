package com.openclassrooms.ycywapi.mapper;


import com.openclassrooms.ycywapi.dto.UserDto;
import com.openclassrooms.ycywapi.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toDto(User user);
    User toEntity(UserDto userDto);
}
