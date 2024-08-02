package example.com.service

import example.com.json.UserInfoJson
import example.com.model.User
import example.com.utils.generatePasswordHash

//class FakeUserService : UserService {
//
//    private val fakeUsers = listOf(
//        User(
//            1,
//            "test@example.com",
//            "ebfd1004426aa76de7903e39a06b552a807c3a516ead76aeba4ce924914a3973035e0d452009486a3e26ff59ee4afe"
//        )
//    )
//
//    override fun authenticateUser(email: String, password: String): User? {
//        fakeUsers.find { it.email == email }?.let {
//            if (it.passwordHash == password.generatePasswordHash(it.passwordSalt)) {
//                return it
//            }
//        }
//        return null
//    }
//
//    override fun registerUser(email: String, password: String) {}
//
//    override fun getUser(id: Int): UserInfoJson? {
//        fakeUsers.find { it.id == id }?.let {
//            return UserInfoJson(it.id, it.email)
//        }
//        return null
//    }
//}