package com.labo.anapath.messaging;

import com.labo.anapath.common.exception.InvalidOperationException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.user.User;
import com.labo.anapath.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock ChatRepository chatRepository;
    @Mock UserRepository userRepository;
    @Mock ChatMapper chatMapper;

    ChatServiceImpl service;

    private final UUID SENDER_ID   = UUID.randomUUID();
    private final UUID RECEIVER_ID = UUID.randomUUID();
    private final UUID BRANCH_ID   = UUID.randomUUID();
    private final UUID CHAT_ID     = UUID.randomUUID();

    @BeforeEach
    void setup() {
        service = new ChatServiceImpl(chatRepository, userRepository, chatMapper);
    }

    private User buildUser(UUID id) {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", id);
        user.setFirstname("Alice");
        user.setLastname("Martin");
        return user;
    }

    private Chat buildChat(UUID senderId, UUID receiverId) {
        User sender   = buildUser(senderId);
        User receiver = buildUser(receiverId);
        Chat chat = new Chat();
        ReflectionTestUtils.setField(chat, "id", CHAT_ID);
        chat.setSender(sender);
        chat.setReceiver(receiver);
        chat.setMessage("Bonjour");
        chat.setIsRead(false);
        return chat;
    }

    @Test
    @DisplayName("send - destinataire valide → crée le message avec isRead=false")
    void send_validReceiver_createsMessage() {
        User sender   = buildUser(SENDER_ID);
        User receiver = buildUser(RECEIVER_ID);
        when(userRepository.findById(SENDER_ID)).thenReturn(Optional.of(sender));
        when(userRepository.findById(RECEIVER_ID)).thenReturn(Optional.of(receiver));
        Chat saved = buildChat(SENDER_ID, RECEIVER_ID);
        when(chatRepository.save(any())).thenReturn(saved);
        ChatResponseDto dto = new ChatResponseDto(CHAT_ID, SENDER_ID, "Alice Martin",
                RECEIVER_ID, "Alice Martin", "Bonjour", false, BRANCH_ID, null);
        when(chatMapper.toResponseDto(saved)).thenReturn(dto);

        ChatRequestDto req = new ChatRequestDto();
        req.setReceiverId(RECEIVER_ID);
        req.setMessage("Bonjour");

        ChatResponseDto result = service.send(req, SENDER_ID, BRANCH_ID);

        assertThat(result.isRead()).isFalse();
        assertThat(result.message()).isEqualTo("Bonjour");
    }

    @Test
    @DisplayName("send - destinataire inconnu → ResourceNotFoundException")
    void send_invalidReceiver_throwsResourceNotFoundException() {
        when(userRepository.findById(SENDER_ID)).thenReturn(Optional.of(buildUser(SENDER_ID)));
        when(userRepository.findById(RECEIVER_ID)).thenReturn(Optional.empty());

        ChatRequestDto req = new ChatRequestDto();
        req.setReceiverId(RECEIVER_ID);
        req.setMessage("Test");

        assertThatThrownBy(() -> service.send(req, SENDER_ID, BRANCH_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("markAsRead - utilisateur non destinataire → InvalidOperationException")
    void markAsRead_notReceiver_throwsInvalidOperationException() {
        UUID otherId = UUID.randomUUID();
        Chat chat = buildChat(SENDER_ID, RECEIVER_ID);
        when(chatRepository.findById(CHAT_ID)).thenReturn(Optional.of(chat));

        assertThatThrownBy(() -> service.markAsRead(CHAT_ID, otherId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("destinataire");
    }

    @Test
    @DisplayName("markAllAsRead - marque tous les messages non lus comme lus")
    void markAllAsRead_updatesAllUnreadMessages() {
        Chat c1 = buildChat(SENDER_ID, RECEIVER_ID);
        Chat c2 = buildChat(SENDER_ID, RECEIVER_ID);
        when(chatRepository.findByReceiverIdAndSenderIdAndIsReadFalse(RECEIVER_ID, SENDER_ID))
                .thenReturn(List.of(c1, c2));
        when(chatRepository.saveAll(any())).thenReturn(List.of(c1, c2));

        int count = service.markAllAsRead(SENDER_ID, RECEIVER_ID);

        assertThat(count).isEqualTo(2);
        verify(chatRepository).saveAll(any());
    }
}
