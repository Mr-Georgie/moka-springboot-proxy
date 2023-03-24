package com.flw.moka.service.entity_service;

import com.flw.moka.entity.CardParams;

public interface CardParamsService {

    CardParams getCardParams(String ref, String method);

    CardParams saveCardParams(CardParams cardParams);

}
