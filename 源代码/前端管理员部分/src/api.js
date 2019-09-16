import axios from 'axios'

axios.defaults.headers.post['Content-Type'] = 'application/json';

export const adminLogin = (data) => {
  return axios.post('api/manager/login', data)
}
export const addAdmin = (data) => {
  return axios.post('api/manager/addManager', data)
}
export const updatePassword = (data) => {
  return axios.post('api/manager/updatePassword', data)
}
export const updateOwnPassword = (data) => {
  return axios.post('api/manager/updateOwnPassword', data)
}
export const ban = (managerId) => {
  return axios.post('api/manager/ban?managerId='+managerId)
}
export const unban = (managerId) => {
  return axios.post('api/manager/unban?managerId='+managerId)
}
export const viewAllManagers = (pageNum, pageSize) => {
  return axios.get('api/manager/viewAllManagers', {
    params: {
      pageNum: pageNum,
      pageSize: pageSize
    }
  })
}
export const viewAllCustomers = (pageNum, pageSize) => {
  return axios.get('api/manager/viewAllCustomers', {
    params: {
      pageNum: pageNum,
      pageSize: pageSize
    }
  })
}
export const viewCustomerInfo = (data) => {
  return axios.get('api/manager/viewCustomerInfo', {params: data})
}
export const banCustomer = (customerId) => {
  return axios.post('api/manager/banCustomer?customerId='+customerId)
}
export const releaseCustomer = (customerId) => {
  return axios.post('api/manager/releaseCustomer?customerId='+customerId)
}
export const viewAllFunds = (pageNum,pageSize) => {
  return axios.get('api/fund/viewAllFunds', {params: {pageNum:pageNum,pageSize:pageSize}})
}

export const getFundDetailedInfo = (data) => {
  return axios.get('api/fund/getFundDetailedInfo', data)
}
export const addFund = (data) => {
  return axios.post('api/fund/manager/addFund', data)
}
