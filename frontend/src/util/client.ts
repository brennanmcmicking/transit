import { useCallback, useMemo } from "react"
import type { Departure, SortBy } from "../data"

const SERVER = window.location.hostname === "localhost" ? "http://localhost:8080" : ""

// interface Client {
//     getBusses: () => Promise<Bus[]>
//     getStops: () => Promise<Stop[]>
//     getNearbyDepartures: (latitude: number, longitude: number, maxDistanceKm: number, sort: SortBy) => Promise<DepartureAndDistance[]>
//     getDeparturesForStop: (stopId: string) => Promise<Departure[]>
//     getDeparturesForRoute: (stopId: string, routeId: string, direction: number, sortBy: SortBy, latitude: number, longitude: number) => Promise<Departure[]>
// }

export type Client = ReturnType<typeof useClient>

export function useClient() {
    const getBusses = useCallback(() => {
        return fetch(`${SERVER}/v1/busses`)
            .then(r => r.json())
    }, [])

    const getStops = useCallback(() => {
        return fetch(`${SERVER}/v1/stops`)
            .then(r => r.json())
    }, [])

    const getNearbyDepartures = useCallback((latitude: number, longitude: number, maxDistanceKm: number, sort: SortBy) => {
        return fetch(`${SERVER}/v1/nearby-departures?latitude=${latitude}&longitude=${longitude}&maxDistanceKm=${maxDistanceKm}&sortBy=${sort}`)
            .then(r => r.json())
    }, [])

    const getDeparturesForStop = useCallback((stopId: string) => {
        return fetch(`${SERVER}/v1/departures-for-stop?stopId=${stopId}`)
            .then(r => r.json())
    }, [])

    const getDeparturesForStopByPosition = useCallback((latitude: number, longitude: number): Promise<Departure[]> => {
        return fetch(`${SERVER}/v1/departures/stop?latitude=${latitude}&longitude=${longitude}`)
            .then(r => r.json())
    }, [])

    const getDeparturesForRoute = useCallback((routeId: string, stopId: string, direction: number, latitude: number, longitude: number) => {
        return fetch(`${SERVER}/v1/departures-for-route/${routeId}/${direction}/${stopId}?latitude=${latitude}&longitude=${longitude}`)
            .then(r => r.json())
    }, [])

    return useMemo(() => {
        return {
            getBusses,
            getStops,
            getNearbyDepartures,
            getDeparturesForStop,
            getDeparturesForStopByPosition,
            getDeparturesForRoute,
        }
    }, [getBusses, getStops, getNearbyDepartures, getDeparturesForStop, getDeparturesForStopByPosition, getDeparturesForRoute])
}