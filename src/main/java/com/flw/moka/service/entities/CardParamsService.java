package com.flw.moka.service.entities;

import com.flw.moka.entity.CardParams;

public interface CardParamsService {

    CardParams getCardParams(String ref, String method);

    CardParams saveCardParams(CardParams cardParams);

}
