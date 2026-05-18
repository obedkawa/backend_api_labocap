package com.labo.anapath.messaging;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "senderName", expression = "java(chat.getSender().getFirstname() + \" \" + chat.getSender().getLastname())")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "receiverName", expression = "java(chat.getReceiver().getFirstname() + \" \" + chat.getReceiver().getLastname())")
    ChatResponseDto toResponseDto(Chat chat);
}
