import { useCallback } from "react"
import type { Bus, Departure, DepartureAndDistance, Stop } from "../data"

const SERVER = "http://localhost:8080"


interface Client {
    getBusses: () => Promise<Bus[]>
    getStops: () => Promise<Stop[]>
    getNearbyDepartures: (latitude: number, longitude: number, maxDistanceKm: number) => Promise<DepartureAndDistance[]>
    getDeparturesForStop: (stopId: string) => Promise<Departure[]>
    getDeparturesForRoute: (stopId: string, routeId: string, direction: number) => Promise<Departure[]>
}

export function useClient(): Client {
    const getBusses = useCallback(() => {
        return fetch(`${SERVER}/v1/busses`)
            .then(r => r.json())
    }, [])

    const getStops = useCallback(() => {
        return fetch(`${SERVER}/v1/stops`)
            .then(r => r.json())
    }, [])

    const getNearbyDepartures = useCallback((latitude: number, longitude: number, maxDistanceKm: number) => {
        return fetch(`${SERVER}/v1/nearby-departures?latitude=${latitude}&longitude=${longitude}&maxDistanceKm=${maxDistanceKm}`)
            .then(r => r.json())
    }, [])

    const getDeparturesForStop = useCallback((stopId: string) => {
        return fetch(`${SERVER}/v1/departures-for-stop?stopId=${stopId}`)
            .then(r => r.json())
    }, [])

    const getDeparturesForRoute = useCallback((routeId: string, stopId: string, direction: number) => {
        return fetch(`${SERVER}/v1/departures-for-route?routeId=${routeId}&stopId=${stopId}&direction=${direction}`)
            .then(r => r.json())
    }, [])

    return {
        getBusses,
        getStops,
        getNearbyDepartures,
        getDeparturesForStop,
        getDeparturesForRoute,
    }
}