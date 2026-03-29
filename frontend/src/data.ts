

export interface Bus {
    id: string
    label: string
    latitude: number
    longitude: number
    speed: number
}

export interface Stop {
    id: string
    name: string
    latitude: number
    longitude: number
    wheelchair: boolean
    code: string
}

export interface Route {
    id: string
    shortName: string
    longName: string
    type: number
    colorHex: string
    textColorHex: string
}

export interface Departure {
    departureTime: number
    route: Route
    direction: number
    tripId: string
    busHeader: string
    stop: Stop
}

export interface DepartureAndDistance {
    departure: Departure
    distance: number
}

export interface StopUpdate {
    stopSequence: number
    stopId: string

    arrival: number
    arrivalDelay: number
    arrivalUncertainty: number

    departure: number
    departureDelay: number
    departureUncertainty: number
}

export interface Trip {
    tripId: string
    startTime: number
    routeId: string
    direction: string
    stopUpdates: StopUpdate[]
}