package com.yxl.smmall.search.service;

import com.yxl.smmall.search.vo.SearchParam;
import com.yxl.smmall.search.vo.SearchResult;
import org.springframework.stereotype.Service;

@Service
public interface MallSearchService {
    SearchResult getList(SearchParam searchParam);
}
