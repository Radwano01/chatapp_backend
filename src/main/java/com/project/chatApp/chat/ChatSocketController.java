package com.project.chatApp.chat;

import com.project.chatApp.chat.dto.*;
import com.project.chatApp.chatRoom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatSocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

    @MessageMapping("/chat.sendMessage")
    public void saveQueue(@Payload CreateDTO createDTO, Principal principal){
        var savedMessage = chatMessageService.privateSave(createDTO, principal.getName());

        var recipientId = chatRoomService.getRecipientId(createDTO.getChatId(), savedMessage.getSenderId());

        var notification = toNotification(savedMessage, recipientId);

        simpMessagingTemplate.convertAndSendToUser(
                recipientId,
                "/queue/chatroom/" + savedMessage.getChatId(),
                notification
        );
        simpMessagingTemplate.convertAndSendToUser(
                savedMessage.getSenderId(),
                "/queue/chatroom/" + savedMessage.getChatId(),
                notification
        );
    }

    @MessageMapping("/chat.group")
    public void saveTopic(@Payload CreateDTO createDTO, Principal principal){
        var savedMessage = chatMessageService.groupSave(createDTO, principal.getName());

        var notification = toNotification(savedMessage, null);
        simpMessagingTemplate.convertAndSend("/topic/chatroom/" + savedMessage.getChatId(), notification);
    }

    @MessageMapping("/chat.deletePrivateMessage")
    public void deleteQueue(@Payload DeleteDTO deleteDTO, Principal principal){
        var deletedMessage = chatMessageService.delete(principal.getName(), deleteDTO.getMessageId());

        var recipientId = chatRoomService.getRecipientId(deletedMessage.getChatId(), deletedMessage.getSenderId());

        simpMessagingTemplate.convertAndSendToUser(
                recipientId,
                "/queue/chatroom/" + deletedMessage.getChatId(),
                deletedMessage
        );

        simpMessagingTemplate.convertAndSendToUser(
                deletedMessage.getSenderId(),
                "/queue/chatroom/" + deletedMessage.getChatId(),
                deletedMessage
        );
    }

    @MessageMapping("/chat.deleteGroupMessage")
    public void deleteTopic(@Payload DeleteDTO deleteDTO, Principal principal){
        var deletedMessage = chatMessageService.delete(principal.getName(), deleteDTO.getMessageId());

        simpMessagingTemplate.convertAndSend(
                "/topic/chatroom/" + deletedMessage.getChatId(),
                deletedMessage
        );
    }

    @MessageMapping("/chat.typing")
    public void typing(@Payload TypingDTO typingDTO, Principal principal){
        typingDTO.setSenderId(principal.getName());
        if(typingDTO.isGroup()){
            simpMessagingTemplate.convertAndSend(
                    "/topic/chatroom/" + typingDTO.getChatId() + "/typing",
                    typingDTO
            );
        }else{
            var getRecipientId = chatRoomService.getRecipientId(typingDTO.getChatId(), typingDTO.getSenderId());
            simpMessagingTemplate.convertAndSendToUser(
                    getRecipientId,
                    "/queue/chatroom/" + typingDTO.getChatId() + "typing",
                    typingDTO
            );
        }
    }

    private ChatNotification toNotification(ChatMessage msg, String recipientId) {
        return ChatNotification.builder()
                .id(msg.getId())
                .chatId(msg.getChatId())
                .senderId(msg.getSenderId())
                .recipientId(recipientId)
                .content(msg.getContent())
                .media(msg.getMedia())
                .avatar(msg.getMedia())
                .senderName(msg.getSenderName())
                .senderAvatar(msg.getSenderAvatar())
                .build();
    }
}
