package com.github.howieyoung91.farseer.data.convert;

import com.github.howieyoung91.farseer.core.pojo.DocumentVo;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

/**
 * @author Howie Young
 * @version 1.0
 * @since 1.0 [2022/08/13 15:55]
 */
public interface DocumentVoConverter<S> extends Converter<S, List<DocumentVo>> {
}
