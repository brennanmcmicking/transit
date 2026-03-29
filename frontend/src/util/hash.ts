export const generateHash = (s: string) => {
    let hash = 0;
    for (const char of s) {
        hash = (hash << 5) - hash + char.charCodeAt(0);
        hash |= 0;
    }
    return hash;
};