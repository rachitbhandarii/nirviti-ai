// Function to fetch JSON data from backend
async function fetchData(url) {
    try {
        console.log('Fetching data from:', url);
        const response = await fetch(url);
        console.log('Response status:', response.status);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        console.log('Data received:', data);
        return data;
    } catch (error) {
        console.error('Error fetching data:', error);
        return null;
    }
}

// Generic table loader functions
function createTableHeaders(table, headers) {
    const thead = table.createTHead();
    const row = thead.insertRow();
    headers.forEach(header => {
        const th = document.createElement('th');
        th.textContent = header;
        row.appendChild(th);
    });
}

function formatDate(dateString) {
    if (!dateString) return 'N/A';
    try {
        // Handle ISO datetime format (e.g., "2025-02-16T09:00:00")
        const date = new Date(dateString);
        
        if (isNaN(date.getTime())) {
            return 'N/A';
        }
        
        // Format the date and time
        return date.toLocaleString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            hour12: true
        });
    } catch (error) {
        console.error('Error formatting date:', error, 'Date string:', dateString);
        return 'N/A';
    }
}

function formatCurrency(amount) {
    if (amount === null || amount === undefined) return '';
    try {
        return `₹ ${parseFloat(amount).toFixed(2)}`;
    } catch (error) {
        console.error('Error formatting currency:', error);
        return amount;
    }
}

function formatPercentage(value) {
    if (value === null || value === undefined) return '';
    try {
        return `${value}%`;
    } catch (error) {
        console.error('Error formatting percentage:', error);
        return value;
    }
}

async function loadTableData(tableId, url, headers, dataFormatter) {
    try {
        console.log('Loading table data...');
        const table = document.getElementById(tableId);
        if (!table) {
            throw new Error(`Table with id ${tableId} not found`);
        }

        // Clear existing table content
        table.innerHTML = '';

        // Create headers
        createTableHeaders(table, headers);

        // Fetch data
        console.log('Fetching data from:', url);
        const response = await fetch(url);
        console.log('Response status:', response.status);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('Data received:', data);

        if (!data || !Array.isArray(data)) {
            throw new Error('Invalid data format received');
        }

        // Create table body
        const tbody = table.createTBody();
        
        if (data.length === 0) {
            const row = tbody.insertRow();
            const cell = row.insertCell();
            cell.colSpan = headers.length;
            cell.textContent = 'No data available';
            cell.style.textAlign = 'center';
            return;
        }

        // Populate table with formatted data
        data.forEach(item => {
            const row = tbody.insertRow();
            const formattedData = dataFormatter(item);
            formattedData.forEach(value => {
                const cell = row.insertCell();
                cell.textContent = value;
            });
        });

    } catch (error) {
        console.error('Error in loadTableData:', error);
        const table = document.getElementById(tableId);
        if (table) {
            const tbody = table.createTBody();
            const row = tbody.insertRow();
            const cell = row.insertCell();
            cell.colSpan = headers.length;
            cell.textContent = 'Error loading data';
            cell.style.textAlign = 'center';
            cell.style.color = 'red';
        }
    }
}

// Example usage:
// loadTableData('myTable', '/api/endpoint', ['Column1', 'Column2', 'Column3'], (item) => [item.patientID, item.patientName, item.age]); 