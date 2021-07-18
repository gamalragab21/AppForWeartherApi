package myappnew.com.appforweartherapi.uitils

import myappnew.com.appforweartherapi.helper.Resource

inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
    return try {
        action()
    } catch(e: Exception) {
        Resource.Error(e.message ?: "An unknown error occured")
    }
}