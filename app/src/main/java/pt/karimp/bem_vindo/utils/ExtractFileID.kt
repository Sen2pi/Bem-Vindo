package pt.karimp.bem_vindo.utils

fun extractFileId(url: String): String? {
    val regex = """files/([a-f0-9\-]+)\/view""".toRegex()
    val matchResult = regex.find(url)
    return matchResult?.groups?.get(1)?.value
}