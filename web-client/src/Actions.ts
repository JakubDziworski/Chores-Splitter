import 'whatwg-fetch'
import {Tasks} from "./Model";
import {Dispatch} from "react-redux";

export const RECEIVE_TASKS = 'RECEIVE_TASKS';
export const RECEIVE_USERS = 'RECEIVE_USERS';

export function fetchTasksAsync() {
    return (dispatch: Dispatch<Tasks>) => {
        fetch('/api/v1/tasks')
            .then(response => response.json())
            .then((body) => {
                    console.log("Response" + body);
                    return dispatch({
                        type: RECEIVE_TASKS,
                        tasks: body.tasks
                    })
                }
            )
        ;
    }
};

export function fetchUsersAsync() {
    return (dispatch: Dispatch<Tasks>) => {
        fetch('/api/v1/users')
            .then(response => response.json())
            .then((body) => {
                    console.log("Response" + body);
                    return dispatch({
                        type: RECEIVE_USERS,
                        users: body.users
                    })
                }
            )
        ;
    }
};