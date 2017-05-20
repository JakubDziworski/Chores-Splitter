import * as React from "react";
import "./App.css";
import {fetchTasksAsync, fetchUsersAsync} from "./Actions";
import {Tasks, Users} from "./Model";
import {connect, Dispatch} from "react-redux";
const logo = require('./logo.svg');
import {TaskChart} from "./presentational/TasksChart"
import {isUndefined} from "util";


class App extends React.Component <AppProps, {}> {

    componentDidMount() {
        this.props.dispatch(fetchTasksAsync());
        this.props.dispatch(fetchUsersAsync());
    }

    render() {
        return (
            <div className="App">
                <div className="App-header">
                    <img src={logo} className="App-logo" alt="logo"/>
                    <h2>Welcome to React</h2>
                </div>
                <TaskChart tasks={this.props.tasks} users={this.props.users}/>

                <p className="App-intro">
                    To get started, edit <code>src/App.tsx</code> and save to reload.
                </p>
            </div>
        );
    }
}

export interface AppProps {
    tasks: Tasks
    users: Users
    dispatch: Dispatch<Tasks>
}

export const AppContainer = connect(
    (state) => {
        console.log(state);
        return {
            tasks : state.tasks,
            users : state.users
        }
    },
    (dispatch) => ({dispatch})
)(App);
