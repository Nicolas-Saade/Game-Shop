const baseURL = import.meta.env.VITE_BASE_URL || 'http://localhost:8080';

const apiFetch = async (endpoint, options = {}) => {
  console.log("REEEEEAAAccctttt", import.meta.env.VITE_BASE_URL);
  const response = await fetch(`${baseURL}${endpoint}`, options);

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`HTTP error! status: ${response.status} - ${errorText}`);
  }

  return response.json();
};

export default apiFetch;