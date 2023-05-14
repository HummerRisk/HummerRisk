import Cookies from 'js-cookie'

const TokenKey = 'Admin-Token'

const ExpiresInKey = 'Admin-Expires-In'

const LicenseKey = 'License'

const isLicense = 'isLicense'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}

export function getLicense() {
  return Cookies.get(LicenseKey)
}

export function setLicense(license) {
  return Cookies.set(LicenseKey, license)
}

export function getIsLicense() {
  return Cookies.get(isLicense)
}

export function setIsLicense(value) {
  return Cookies.set(isLicense, value)
}

export function removeLicense() {
  return Cookies.remove(LicenseKey)
}

export function getExpiresIn() {
  return Cookies.get(ExpiresInKey) || -1
}

export function setExpiresIn(time) {
  return Cookies.set(ExpiresInKey, time)
}

export function removeExpiresIn() {
  return Cookies.remove(ExpiresInKey)
}
