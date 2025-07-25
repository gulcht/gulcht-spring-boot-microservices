package com.gulcht.microservices.order.stubs;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class InventoryClientStub {
    public static void stubInventoryCall(String skuCode, Integer quantity) {
        stubFor(get(urlPathEqualTo("/api/inventory"))
                .withQueryParam("skuCode", equalTo(skuCode))
                .withQueryParam("quantity", equalTo(quantity.toString()))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")));
    }
}
