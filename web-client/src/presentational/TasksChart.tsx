import * as React from 'react'
import {Task, Tasks, User, Users} from "../Model";
import RC2 from 'react-chartjs2';
import R = require("ramda");
import L = require("lodash");
import {isUndefined} from "util";


export interface TasksChartProps {
    tasks: Tasks
    users: Users
}

export const TaskChart: React.StatelessComponent<TasksChartProps> = ({tasks, users}) => {
        const randomColor = () => {
            const letters = '0123456789ABCDEF';
            let color = '#';
            for (var i = 0; i < 6; i++) {
                color += letters[Math.floor(Math.random() * 16)];
            }
            return color;
        };

        const usersTasks = (userId: number) => tasks.tasks.filter(it => it.userId == userId);

        if (isUndefined(users.users) || isUndefined((tasks.tasks))) {
            return (<div>
                Loading
            </div>);
        }
        function pointsUntilTask(task: Task): number {
            const allTasksForUser = usersTasks(task.userId);
            const olderThan = L.filter(allTasksForUser, it => it.assignedAt < task.assignedAt);
            return olderThan
                .map(it => it.chore.points)
                .reduce((curr, total) => total + curr, task.chore.points);
        }

        const chartData = {
            datasets: users.users.map(user => {
                return {
                    borderColor: randomColor(),
                    fill: false,
                    label: user.name,
                    data: usersTasks(user.id).map(task => ({
                        x: task.assignedAt,
                        y: pointsUntilTask(task),
                    }))
                }
            })
        };
        const chartOptions = {
            title: {
                display: true,
                text: 'Tasks'
            }
        };
        return (<RC2 data={chartData} options={chartOptions} type="scatter"/>);
    }
;