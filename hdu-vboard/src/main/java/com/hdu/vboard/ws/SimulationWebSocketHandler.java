package com.hdu.vboard.ws;

import com.hdu.vboard.executor.SimulationWorker;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.json.JSONObject;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.hdu.vboard.executor.SimulationWorker.stopSimulationWorker;
import static com.hdu.vboard.executor.SimulationWorker.Workers;

@Slf4j
public class SimulationWebSocketHandler extends TextWebSocketHandler {

  private static final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

  public static WebSocketSession getSession(String sessionId) {
    for (WebSocketSession session : sessions) {
      if (session.getId().equals(sessionId)) {
        return session;
      }
    }
    return null;
  }

  @SneakyThrows
  public static void sendErrorMessage(WebSocketSession session, String errorMessage) {
    JSONObject errorResponse = new JSONObject()
        .put("type", "error")
        .put("message", errorMessage);
    session.sendMessage(new TextMessage(errorResponse.toString()));
    log.debug(errorResponse.toString());
  }

  @SneakyThrows
  public static void sendAckMessage(WebSocketSession session, String message) {
    JSONObject ackResponse = new JSONObject()
        .put("type", "ack")
        .put("message", message);
    session.sendMessage(new TextMessage(ackResponse.toString()));
    log.debug(ackResponse.toString());
  }

  @SneakyThrows
  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) {
    sessions.add(session);
    log.info("New ws connection established: {}", session.getId());
    session.sendMessage(new TextMessage(new JSONObject()
        .put("type", "sessionId")
        .put("sessionId", session.getId())
        .toString()));
  }

  @Override
  @SneakyThrows
  protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
    String payload = message.getPayload();
    JSONObject json = new JSONObject(payload);

    String type = json.getStr("type");
    String sessionId = json.getStr("sessionId");

    if ("signal".equals(type)) {
      String signalData = json.getStr("data");
      SimulationWorker worker = Workers.get(sessionId);

      if (worker == null) {
        log.error("No worker running for sessionId: {}", sessionId);
        sendErrorMessage(session, "No simulation running for sessionId " + sessionId);
        return;
      }

      try {
        sendAckMessage(session, "Signal received successfully.");
        worker.SendSignal(signalData);
      } catch (IOException e) {
        sendErrorMessage(session, "Error sending signal: " + e.getMessage());
      }
    }
  }

  @Override
  public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
    sessions.remove(session);
    boolean f = stopSimulationWorker(session.getId());
    if (f) {
      log.info("Manually stopping simulation worker for sessionId: {}", session.getId());
    }
    log.info("ws connection closed: {}", session.getId());
  }
}
