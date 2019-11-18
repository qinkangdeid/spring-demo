import com.intellij.psi.*
import com.itangcent.common.model.Request
import com.itangcent.idea.plugin.api.export.ClassExporter
import com.itangcent.idea.plugin.api.export.SpringRequestClassExporter
import com.itangcent.idea.plugin.api.export.yapi.YapiClassExportRuleKeys
import com.itangcent.idea.plugin.api.export.yapi.YapiRequestKitKt
import com.itangcent.idea.plugin.script.ActionExt
import com.itangcent.idea.plugin.utils.KtHelper
import com.itangcent.intellij.context.ActionContext

import java.util.stream.Collectors
import java.util.stream.Stream

class YapiExportActionExt implements ActionExt {

    void init(ActionContext.ActionContextBuilder builder) {

        builder.bind(ClassExporter.class, KtHelper.INSTANCE.ktFunction({
            it.to(CustomClassExporter.class).in(com.google.inject.Singleton.class)
            return null
        }))

    }

    static class CustomClassExporter extends SpringRequestClassExporter {

        void processCompleted(PsiMethod method, Request request) {
            super.processCompleted(method, request)

            String tags = ruleComputer.computer(YapiClassExportRuleKeys.TAG, method)
            if (tags != null && !tags.isEmpty()) {
                YapiRequestKitKt.setTags(request, Stream.of(tags.split("\n"))
                        .map { it.trim() }
                        .filter { !it.isEmpty() }
                        .collect(Collectors.toList())
                )
            }

            String status = ruleComputer.computer(YapiClassExportRuleKeys.STATUS, method)
            logger.info(YapiRequestKitKt.class.toString())
            YapiRequestKitKt.setStatus(request, status)
        }
    }
}


