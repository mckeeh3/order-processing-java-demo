package io.mystore.stock.entity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.akkaserverless.javasdk.eventsourcedentity.EventSourcedEntityContext;
import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mystore.stock.api.StockOrderApi;
import io.mystore.stock.api.StockSkuItemApi;
import io.mystore.stock.api.StockSkuItemCommands;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

public class StockOrder extends AbstractStockOrder {
  static final Logger log = LoggerFactory.getLogger(StockOrder.class);

  public StockOrder(EventSourcedEntityContext context) {
  }

  @Override
  public StockOrderEntity.StockOrderState emptyState() {
    return StockOrderEntity.StockOrderState.getDefaultInstance();
  }

  @Override
  public Effect<Empty> createStockOrder(StockOrderEntity.StockOrderState state, StockOrderApi.CreateStockOrderCommand command) {
    return reject(state, command).orElseGet(() -> handle(state, command));
  }

  @Override
  public Effect<Empty> joinStockSkuItem(StockOrderEntity.StockOrderState state, StockSkuItemCommands.JoinStockSkuItemCommand command) {
    return handle(state, command);
  }

  @Override
  public Effect<Empty> releaseStockSkuItem(StockOrderEntity.StockOrderState state, StockSkuItemCommands.ReleaseStockSkuItemCommand command) {
    return handle(state, command);
  }

  @Override
  public Effect<StockOrderApi.StockOrder> getStockOrder(StockOrderEntity.StockOrderState state, StockOrderApi.GetStockOrderRequest command) {
    return reject(state, command).orElseGet(() -> handle(state, command));
  }

  @Override
  public StockOrderEntity.StockOrderState stockOrderCreated(StockOrderEntity.StockOrderState state, StockOrderEntity.StockOrderCreated event) {
    return state
        .toBuilder()
        .setStockOrderId(event.getStockOrderId())
        .setSkuId(event.getSkuId())
        .setSkuName(event.getSkuName())
        .setQuantity(event.getQuantity())
        .addAllStockSkuItems(toStockSkuItemsList(event))
        .build();
  }

  @Override
  public StockOrderEntity.StockOrderState stockSkuItemJoined(StockOrderEntity.StockOrderState state, StockOrderEntity.StockSkuItemJoined event) {
    var stockSkuItems = toStockSkuItemsList(state, event);

    return state
        .toBuilder()
        .setShippedUtc(areAllItemsShipped(stockSkuItems))
        .clearStockSkuItems()
        .addAllStockSkuItems(stockSkuItems)
        .build();
  }

  @Override
  public StockOrderEntity.StockOrderState stockSkuItemReleased(StockOrderEntity.StockOrderState state, StockOrderEntity.StockSkuItemReleased event) {
    throw new RuntimeException("The event handler for `StockSkuItemReleased` is not implemented, yet");
  }

  private Optional<Effect<Empty>> reject(StockOrderEntity.StockOrderState state, StockOrderApi.CreateStockOrderCommand command) {
    if (command.getStockOrderId().isEmpty()) {
      return Optional.of(effects().error("stockOrderId is empty"));
    }
    if (command.getSkuId().isEmpty()) {
      return Optional.of(effects().error("skuId is empty"));
    }
    if (command.getSkuName().isEmpty()) {
      return Optional.of(effects().error("skuName is empty"));
    }
    if (command.getQuantity() <= 0) {
      return Optional.of(effects().error("quantity is <= 0"));
    }
    return Optional.empty();
  }

  private Optional<Effect<StockOrderApi.StockOrder>> reject(StockOrderEntity.StockOrderState state, StockOrderApi.GetStockOrderRequest command) {
    if (state.getStockOrderId().isEmpty()) {
      return Optional.of(effects().error("stock order has not been created yet"));
    }
    return Optional.empty();
  }

  private Effect<Empty> handle(StockOrderEntity.StockOrderState state, StockOrderApi.CreateStockOrderCommand command) {
    return effects()
        .emitEvent(eventFor(state, command))
        .thenReply(newState -> Empty.getDefaultInstance());
  }

  private Effect<Empty> handle(StockOrderEntity.StockOrderState state, StockSkuItemCommands.JoinStockSkuItemCommand command) {
    return effects()
        .emitEvent(eventFor(state, command))
        .thenReply(newState -> Empty.getDefaultInstance());
  }

  private Effect<Empty> handle(StockOrderEntity.StockOrderState state, StockSkuItemCommands.ReleaseStockSkuItemCommand command) {
    return effects()
        .emitEvent(eventFor(state, command))
        .thenReply(newState -> Empty.getDefaultInstance());
  }

  private Effect<StockOrderApi.StockOrder> handle(StockOrderEntity.StockOrderState state, StockOrderApi.GetStockOrderRequest command) {
    return effects()
        .reply(StockOrderApi.StockOrder
            .newBuilder()
            .setStockOrderId(state.getStockOrderId())
            .setSkuId(state.getSkuId())
            .setSkuName(state.getSkuName())
            .setQuantity(state.getQuantity())
            .setShippedUtc(state.getShippedUtc())
            .addAllStockSkuItems(toApi(state.getStockSkuItemsList()))
            .build());
  }

  private StockOrderEntity.StockOrderCreated eventFor(StockOrderEntity.StockOrderState state, StockOrderApi.CreateStockOrderCommand command) {
    return StockOrderEntity.StockOrderCreated
        .newBuilder()
        .setStockOrderId(command.getStockOrderId())
        .setSkuId(command.getSkuId())
        .setSkuName(command.getSkuName())
        .setQuantity(command.getQuantity())
        .build();
  }

  private StockOrderEntity.StockSkuItemJoined eventFor(StockOrderEntity.StockOrderState state, StockSkuItemCommands.JoinStockSkuItemCommand command) {
    return StockOrderEntity.StockSkuItemJoined
        .newBuilder()
        .setStockOrderId(state.getStockOrderId())
        .setSkuId(command.getSkuId())
        .setSkuItemId(command.getSkuItemId())
        .setOrderId(command.getOrderId())
        .setOrderSkuItemId(command.getOrderSkuItemId())
        .build();
  }

  private StockOrderEntity.StockSkuItemReleased eventFor(StockOrderEntity.StockOrderState state, StockSkuItemCommands.ReleaseStockSkuItemCommand command) {
    return StockOrderEntity.StockSkuItemReleased
        .newBuilder()
        .setStockOrderId(state.getStockOrderId())
        .setSkuId(command.getSkuId())
        .setSkuItemId(command.getSkuItemId())
        .setOrderId(command.getOrderId())
        .setOrderSkuItemId(command.getOrderSkuItemId())
        .build();
  }

  private List<StockOrderEntity.StockSkuItem> toStockSkuItemsList(StockOrderEntity.StockOrderCreated event) {
    return IntStream.range(0, event.getQuantity())
        .mapToObj(i -> StockOrderEntity.StockSkuItem.newBuilder()
            .setSkuItemId(UUID.randomUUID().toString())
            .setSkuId(event.getSkuId())
            .setSkuName(event.getSkuName())
            .setOrderId(event.getStockOrderId())
            .build())
        .collect(Collectors.toList());
  }

  private List<StockOrderEntity.StockSkuItem> toStockSkuItemsList(StockOrderEntity.StockOrderState state, StockOrderEntity.StockSkuItemJoined event) {
    return state.getStockSkuItemsList().stream()
        .map(stockSkuItem -> {
          if (stockSkuItem.getSkuId().equals(event.getSkuId())) {
            return stockSkuItem.toBuilder()
                .setOrderId(event.getStockOrderId())
                .setOrderSkuItemId(event.getOrderSkuItemId())
                .setShippedUtc(event.getShippedUtc())
                .build();
          } else {
            return stockSkuItem;
          }
        })
        .collect(Collectors.toList());
  }

  private Timestamp areAllItemsShipped(List<StockOrderEntity.StockSkuItem> stockSkuItems) {
    return stockSkuItems.stream()
        .filter(item -> item.getShippedUtc() == null || item.getShippedUtc().getSeconds() == 0)
        .findFirst()
        .isPresent() ? timestampZero() : timestampNow();
  }

  private List<StockSkuItemApi.StockSkuItem> toApi(List<StockOrderEntity.StockSkuItem> stockSkuItems) {
    return stockSkuItems.stream()
        .map(stockSkuItem -> StockSkuItemApi.StockSkuItem
            .newBuilder()
            .setStockOrderId(stockSkuItem.getStockOrderId())
            .setSkuItemId(stockSkuItem.getSkuItemId())
            .setSkuId(stockSkuItem.getSkuId())
            .setSkuName(stockSkuItem.getSkuName())
            .setOrderId(stockSkuItem.getOrderId())
            .setOrderSkuItemId(stockSkuItem.getOrderSkuItemId())
            .setShippedUtc(stockSkuItem.getShippedUtc())
            .build())
        .collect(Collectors.toList());
  }

  static Timestamp timestampZero() {
    return Timestamp
        .newBuilder()
        .setSeconds(0)
        .setNanos(0)
        .build();
  }

  static Timestamp timestampNow() {
    var now = Instant.now();
    return Timestamp
        .newBuilder()
        .setSeconds(now.getEpochSecond())
        .setNanos(now.getNano())
        .build();
  }
}