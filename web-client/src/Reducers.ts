import {Reducer} from "redux";
import {Task, Tasks, User, Users} from "./Model";
import {RECEIVE_TASKS, RECEIVE_USERS} from "./Actions";

const emptyTasks: Tasks = {
    tasks: [] as Task[]
};

const emptyUsers: Users = {
    users: [] as User[]
};

export const tasksReducer: Reducer<Tasks> = (state = emptyTasks, action) => {
    switch (action.type) {
        case RECEIVE_TASKS:
            return {tasks: action.tasks};
        default:
            return state
    }
};

export const usersReducer: Reducer<Users> = (state = emptyUsers, action) => {
    switch (action.type) {
        case RECEIVE_USERS:
            return {users: action.users};
        default:
            return state
    }
};