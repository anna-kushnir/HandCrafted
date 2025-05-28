function parsePhotosString(str) {
    return str
        .replace(/^\[|\]$/g, '')
        .split(',')
        .map(s => s.trim())
        .filter(s => s.length > 0);
}