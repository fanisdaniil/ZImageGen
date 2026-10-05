package gr.zimagegen.security

interface ApiKeyProvider {

    fun getBflApiKey(): String
}