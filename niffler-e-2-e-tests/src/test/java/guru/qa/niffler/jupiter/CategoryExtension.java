package guru.qa.niffler.jupiter;

import guru.qa.niffler.api.CategoryService;
import guru.qa.niffler.model.CategoryJson;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class CategoryExtension implements BeforeEachCallback, ParameterResolver {

    public static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);

    private static final OkHttpClient httpClient = new OkHttpClient.Builder().build();
    private static final Retrofit retrofit = new Retrofit.Builder().client(httpClient).baseUrl("http://127.0.0.1:3000").addConverterFactory(JacksonConverterFactory.create())
            .build();
    ;

    private final CategoryService categoryService = retrofit.create(CategoryService.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Category annotation = context.getRequiredTestMethod().getAnnotation(Category.class);
        if (annotation != null) {
            CategoryJson categoryJson = new CategoryJson();
            categoryJson.setCategory(annotation.category());
            categoryJson.setUsername(annotation.username());
            CategoryJson categoryJson1 = categoryService.addCategory(categoryJson).execute().body();
            context.getStore(NAMESPACE).put("category", categoryJson1);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get("category", CategoryJson.class);
    }
}
