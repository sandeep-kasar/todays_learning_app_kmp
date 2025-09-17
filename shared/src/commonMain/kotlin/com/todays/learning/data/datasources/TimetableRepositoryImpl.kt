package com.todays.learning.data.datasources


import com.todays.learning.data.mappers.toDomain
import com.todays.learning.data.network.models.subject.SubjectResultDto
import com.todays.learning.data.network.models.timetable.TimetableResultDto
import com.todays.learning.data.network.utils.safeApiCall
import com.todays.learning.domain.models.Subject
import com.todays.learning.domain.models.TimeTable
import com.todays.learning.domain.repositories.TimetableRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments
import kotlinx.coroutines.flow.Flow

class TimetableRepositoryImpl(
    private val httpClient: HttpClient
) : TimetableRepository {


    override suspend fun fetchTodaysTimetable(
        standard: String,
        division: String
    ): Result<Flow<List<TimeTable>?>> {
        return safeApiCall {

            val response = httpClient.get(urlString = "timetable") {
                url {
                    appendPathSegments(standard)
                    appendPathSegments(division)
                }
            }.body<TimetableResultDto>()

            response.result?.map { it.toDomain() }
        }
    }

    override suspend fun fetchSubjectDetails(subject: String): Result<Flow<Subject?>> {
        return safeApiCall {
            val response = httpClient.get {
                url {
                    appendPathSegments(subject)
                }
            }.body<SubjectResultDto>()

            response.result?.toDomain()
        }
    }
}
