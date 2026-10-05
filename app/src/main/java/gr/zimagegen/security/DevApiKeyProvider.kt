package gr.zimagegen.security
import gr.zimagegen.BuildConfig
class DevApiKeyProvider : ApiKeyProvider {

    override fun getBflApiKey(): String {
        return BuildConfig.BFL_API_KEY
    }
}