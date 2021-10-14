/* eslint-disable no-unused-expressions */
/* eslint-disable no-param-reassign */
/* eslint-disable class-methods-use-this */
import React from 'react'
import axios, {
    AxiosInstance,
    AxiosRequestConfig,
    AxiosResponse,
    AxiosError,
} from 'axios'
import TokenService from './token-service'
import useErrorStatus from '../hooks/useErrorStatus'
import EventBus from '../common/EventBus'

type TError = { error: AxiosError | ResponseError }
interface ResponseError extends Error {
    status?: number
}
enum StatusCode {
    Unauthorized = 401,
    Forbidden = 403,
    TooManyRequests = 429,
    InternalServerError = 500,
}

const headers: Readonly<Record<string, string | boolean>> = {
    Accept: 'application/json',
    'Content-Type': 'application/json',
    // 'Access-Control-Allow-Credentials': true,
    // 'X-Requested-With': 'XMLHttpRequest',
}
const { getLocalAccessToken } = TokenService
const injectToken = (config: AxiosRequestConfig): AxiosRequestConfig => {
    try {
        const token = getLocalAccessToken()
        console.log(token)
        if (token !== null || token !== undefined) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    } catch (error: any) {
        throw new Error('Error!')
    }
}

class Http {
    private instance: AxiosInstance | null = null

    private get http(): AxiosInstance {
        return this.instance != null ? this.instance : this.initHttp()
    }

    initHttp() {
        const http = axios.create({
            baseURL: 'http://ultrasound.us-east-1.elasticbeanstalk.com/api/',
            headers,
            withCredentials: true,
        })

        http.interceptors.request.use(injectToken, (error) =>
            Promise.reject(error)
        )

        http.interceptors.response.use(
            (response) => response,
            (error: Error | AxiosError) => {
                if (axios.isAxiosError(error) && error.response !== undefined) {
                    this.handleError(error.response?.status)
                }
                throw new Error(error.message)
            }
        )
        this.instance = http
        return http
    }

    request<T = any, R = AxiosResponse<T>>(
        config: AxiosRequestConfig
    ): Promise<R> {
        return this.http.request(config)
    }

    get<T = any, R = AxiosResponse<T>>(
        url: string,
        config?: AxiosRequestConfig
    ): Promise<R> {
        return this.http.get<T, R>(url, config)
    }

    post<T = any, R = AxiosResponse<T>>(
        url: string,
        data?: T,
        config?: AxiosRequestConfig
    ): Promise<R> {
        return this.http.post<T, R>(url, data, config)
    }

    put<T = any, R = AxiosResponse<T>>(
        url: string,
        data?: T,
        config?: AxiosRequestConfig
    ): Promise<R> {
        return this.http.put<T, R>(url, data, config)
    }

    delete<T = any, R = AxiosResponse<T>>(
        url: string,
        config?: AxiosRequestConfig
    ): Promise<R> {
        return this.http.delete<T, R>(url, config)
    }

    // Handle global app errors
    // We can handle generic app errors depending on the status code
    private handleError(response: number) {
        switch (response) {
            case StatusCode.InternalServerError:
                // history.push('/home')
                EventBus.dispatch('logout')
                Promise.reject(new Error('Internal Server Error'))
                break

            case StatusCode.Forbidden:
                // history.push('/home')
                EventBus.dispatch('logout')
                Promise.reject(new Error('Forbidden'))
                break

            case StatusCode.Unauthorized:
                // history.push('/home')
                EventBus.dispatch('logout')
                Promise.reject(new Error('Unauthorized'))

                break

            // case StatusCode.TooManyRequests:
            // Handle TooManyRequests
            // break
            // no default
        }
        return Promise.reject(response)
    }
}

export const api = new Http()
