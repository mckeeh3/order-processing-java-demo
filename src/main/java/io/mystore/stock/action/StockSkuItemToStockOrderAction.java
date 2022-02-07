package io.mystore.stock.action;

import com.akkaserverless.javasdk.action.ActionCreationContext;
import com.google.protobuf.Any;
import com.google.protobuf.Empty;

import io.mystore.stock.api.StockOrderApi;
import io.mystore.stock.entity.StockSkuItemEntity;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

public class StockSkuItemToStockOrderAction extends AbstractStockSkuItemToStockOrderAction {

  public StockSkuItemToStockOrderAction(ActionCreationContext creationContext) {
  }

  @Override
  public Effect<Empty> onJoinedToStockSkuItem(StockSkuItemEntity.JoinedToStockSkuItem joinedToStockSkuItem) {
    return effects().forward(components().stockOrder().joinStockSkuItem(toStockOrder(joinedToStockSkuItem)));
  }

  @Override
  public Effect<Empty> onReleasedFromStockSkuItem(StockSkuItemEntity.ReleasedFromStockSkuItem releasedFromStockSkuItem) {
    return effects().forward(components().stockOrder().releaseStockSkuItem(toStockOrder(releasedFromStockSkuItem)));
  }

  @Override
  public Effect<Empty> ignoreOtherEvents(Any any) {
    return effects().reply(Empty.getDefaultInstance());
  }

  private StockOrderApi.JoinStockSkuItemToStockOrderCommand toStockOrder(StockSkuItemEntity.JoinedToStockSkuItem joinedToStockSkuItem) {
    return StockOrderApi.JoinStockSkuItemToStockOrderCommand
        .newBuilder()
        .setStockOrderId(joinedToStockSkuItem.getStockOrderId())
        .setSkuId(joinedToStockSkuItem.getSkuId())
        .setStockSkuItemId(joinedToStockSkuItem.getStockSkuItemId())
        .setOrderId(joinedToStockSkuItem.getOrderId())
        .setOrderSkuItemId(joinedToStockSkuItem.getOrderSkuItemId())
        .setShippedUtc(joinedToStockSkuItem.getShippedUtc())
        .build();
  }

  private StockOrderApi.ReleaseStockSkuItemFromStockOrderCommand toStockOrder(StockSkuItemEntity.ReleasedFromStockSkuItem releasedFromStockSkuItem) {
    return StockOrderApi.ReleaseStockSkuItemFromStockOrderCommand
        .newBuilder()
        .setStockOrderId(releasedFromStockSkuItem.getStockOrderId())
        .setSkuId(releasedFromStockSkuItem.getSkuId())
        .setStockSkuItemId(releasedFromStockSkuItem.getStockSkuItemId())
        .setOrderId(releasedFromStockSkuItem.getOrderId())
        .setOrderSkuItemId(releasedFromStockSkuItem.getOrderSkuItemId())
        .build();
  }
}
