package com.yxl.smmall.search.service;

import com.yxl.common.to.es.SkuEsModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface ProductSaveService {

    Boolean saveEsModel(List<SkuEsModel> skuEsModels) throws IOException;
}
