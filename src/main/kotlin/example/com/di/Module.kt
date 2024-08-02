package example.com.di

import Database
import example.com.data.DatabaseImpl
import example.com.service.UserService
import org.koin.dsl.module

val appModule = module {
    single<Database> { DatabaseImpl() }
    single { UserService(get()) }
}