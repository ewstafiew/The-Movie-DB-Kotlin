package com.example.moviedb.di

import com.example.moviedb.data.repository.UserRepository
import com.example.moviedb.fake.FakeUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
object TestRepositoryModule {

    @Provides
    @Singleton
    fun provideFakeUserRepository(): FakeUserRepository = FakeUserRepository()

    @Provides
    @Singleton
    fun provideUserRepository(fakeUserRepository: FakeUserRepository): UserRepository = fakeUserRepository
}

