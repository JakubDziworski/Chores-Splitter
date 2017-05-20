import { createStore,applyMiddleware, combineReducers } from 'redux'
import {tasksReducer, usersReducer} from './Reducers'
import thunkMiddleware from "redux-thunk";

const reducers = combineReducers(
    {
        users:usersReducer,
        tasks:tasksReducer
    }
);
export const store = createStore(reducers,applyMiddleware(thunkMiddleware));