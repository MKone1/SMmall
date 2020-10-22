package com.yxl.common.vo;

import lombok.Data;

import java.util.List;

@Data
public class MergeVO {
  private Long puchaseId;
  private List<Long> items;
}
