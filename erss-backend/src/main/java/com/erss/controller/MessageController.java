package com.erss.controller;

import com.erss.common.ApiResponse;
import com.erss.service.MessageService;
import com.erss.vo.NotificationVO;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
  private final MessageService messageService;

  public MessageController(MessageService messageService) {
    this.messageService = messageService;
  }

  @GetMapping
  public ApiResponse<List<NotificationVO>> list(@RequestParam(required = false) String category) {
    return ApiResponse.ok(messageService.listMine(category));
  }

  @PostMapping("/{id}/read")
  public ApiResponse<Void> markRead(@PathVariable Long id) {
    messageService.markRead(id);
    return ApiResponse.ok();
  }

  @PostMapping("/read-all")
  public ApiResponse<Void> markAllRead() {
    messageService.markAllRead();
    return ApiResponse.ok();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable Long id) {
    messageService.deleteMine(id);
    return ApiResponse.ok();
  }
}
