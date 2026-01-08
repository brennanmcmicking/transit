

export interface Bus {
    id: string
    label: string
    latitude: number
    longitude: number
    speed: number
}

export interface Departure {
    departureTime: number
    routeId: string
    stopId: string
}

export interface Stop {
    id: string
    name: string
    site: string
    latitude: number
    longitude: number
    sysCode: string
    system: string
    municipality: string
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