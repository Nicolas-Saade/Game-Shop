const baseURL = process.env.REACT_APP_BASE_URL || 'http://localhost:8080';

const apiFetch = async (endpoint, options = {}) => {
  const response = await fetch(`${baseURL}${endpoint}`, options);

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`HTTP error! status: ${response.status} - ${errorText}`);
  }

  return response.json();
};

export default apiFetch;