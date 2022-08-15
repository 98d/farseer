package com.github.howieyoung91.farseer.data;

import com.github.howieyoung91.farseer.core.pojo.DocumentVo;
import com.github.howieyoung91.farseer.data.remote.RemoteIndexController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author Howie Young
 * @version 1.0
 * @since 1.0 [2022/08/14 16:36]
 */
@Component
@Slf4j
public class IndexListener<S> {
    @Autowired(required = false)
    private DocumentSource<S>     source;
    @Autowired
    private RemoteIndexController indexController;

    @PostConstruct
    public void listen() {
        if (source == null) {
            return;
        }
        while (true) {
            List<DocumentVo> documentVos = source.getDocuments();
            if (!documentVos.isEmpty()) {
                for (DocumentVo documentVo : documentVos) {
                    indexController.index(documentVo);
                    log.info("{}", documentVo);
                }
            }
            else {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
