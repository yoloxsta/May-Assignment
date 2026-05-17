// Backend API URL - configured via environment or default to localhost:8080
const API_URL = (window.BACKEND_URL || 'http://localhost:8080') + '/api/tasks';

// Load tasks when page loads
document.addEventListener('DOMContentLoaded', loadTasks);

// Handle form submission
document.getElementById('taskForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const input = document.getElementById('taskInput');
    const title = input.value.trim();
    
    if (!title) return;
    
    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ title })
        });
        
        if (response.ok) {
            input.value = '';
            loadTasks();
        }
    } catch (error) {
        showError('Failed to add task. Is backend running?');
    }
});

async function loadTasks() {
    const taskList = document.getElementById('taskList');
    taskList.innerHTML = '<div class="loading">Loading tasks...</div>';
    
    try {
        const response = await fetch(API_URL);
        const tasks = await response.json();
        
        if (tasks.length === 0) {
            taskList.innerHTML = '<div class="loading">No tasks yet. Add one above!</div>';
        } else {
            taskList.innerHTML = tasks.map(task => createTaskHTML(task)).join('');
        }
        
        updateStats(tasks);
    } catch (error) {
        taskList.innerHTML = '<div class="error">Failed to load tasks. Is the backend running on port 8080?</div>';
    }
}

function createTaskHTML(task) {
    return `
        <div class="task-item ${task.completed ? 'completed' : ''}" data-id="${task.id}">
            <div class="checkbox" onclick="toggleTask(${task.id})"></div>
            <span class="task-title">${escapeHtml(task.title)}</span>
            <button class="delete-btn" onclick="deleteTask(${task.id})">Delete</button>
        </div>
    `;
}

async function toggleTask(id) {
    try {
        const response = await fetch(`${API_URL}?id=${id}`, {
            method: 'PUT'
        });
        
        if (response.ok) {
            loadTasks();
        }
    } catch (error) {
        showError('Failed to update task');
    }
}

async function deleteTask(id) {
    if (!confirm('Delete this task?')) return;
    
    try {
        const response = await fetch(`${API_URL}?id=${id}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            loadTasks();
        }
    } catch (error) {
        showError('Failed to delete task');
    }
}

function updateStats(tasks) {
    const total = tasks.length;
    const completed = tasks.filter(t => t.completed).length;
    const pending = total - completed;
    
    document.getElementById('stats').textContent = 
        `${total} tasks • ${completed} completed • ${pending} pending`;
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function showError(message) {
    const taskList = document.getElementById('taskList');
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error';
    errorDiv.textContent = message;
    taskList.prepend(errorDiv);
    
    setTimeout(() => errorDiv.remove(), 3000);
}
