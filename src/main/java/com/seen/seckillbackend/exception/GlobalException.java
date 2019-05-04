package com.seen.seckillbackend.exception;

import com.seen.seckillbackend.util.CodeMsg;
import lombok.Data;

@Data
public class GlobalException extends RuntimeException {
    private CodeMsg cm;

    public GlobalException(CodeMsg cm) {
        super(cm.toString());
        this.cm = cm;
    }

}
