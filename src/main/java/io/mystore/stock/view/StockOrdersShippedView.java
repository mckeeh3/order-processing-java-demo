package io.mystore.stock.view;

import com.akkaserverless.javasdk.view.View;
import com.akkaserverless.javasdk.view.ViewContext;
import com.google.protobuf.Any;
import io.mystore.stock.entity.StockOrderEntity;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

public class StockOrdersShippedView extends AbstractStockOrdersShippedView {

  public StockOrdersShippedView(ViewContext context) {
  }

  @Override
  public StockOrdersModel.StockOrder emptyState() {
    return StockOrdersModel.StockOrder.getDefaultInstance();
  }

  @Override
  public View.UpdateEffect<StockOrdersModel.StockOrder> onStockOrderCreated(
      StockOrdersModel.StockOrder state, StockOrderEntity.StockOrderCreated stockOrderCreated) {
    return effects().updateState(
        StockOrderEventHandler
            .fromState(state)
            .handle(stockOrderCreated)
            .toState());
  }

  @Override
  public View.UpdateEffect<StockOrdersModel.StockOrder> onStockSkuItemJoined(
      StockOrdersModel.StockOrder state, StockOrderEntity.StockSkuItemJoined stockSkuItemJoined) {
    return effects().updateState(
        StockOrderEventHandler
            .fromState(state)
            .handle(stockSkuItemJoined)
            .toState());
  }

  @Override
  public View.UpdateEffect<StockOrdersModel.StockOrder> onStockSkuItemReleased(
      StockOrdersModel.StockOrder state, StockOrderEntity.StockSkuItemReleased stockSkuItemReleased) {
    return effects().updateState(
        StockOrderEventHandler
            .fromState(state)
            .handle(stockSkuItemReleased)
            .toState());
  }

  @Override
  public View.UpdateEffect<StockOrdersModel.StockOrder> ignoreOtherEvents(StockOrdersModel.StockOrder state, Any any) {
    return effects().ignore();
  }
}
