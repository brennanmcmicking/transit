export function minutesFromEpoch(epochSeconds: number) {
    const nowMilliseconds = Date.now();
    const differenceMilliseconds = epochSeconds * 1000 - nowMilliseconds;
    const differenceMinutes = differenceMilliseconds / 60000;
    return Math.floor(differenceMinutes);
}