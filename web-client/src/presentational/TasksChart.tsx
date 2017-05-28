import * as React from 'react';
import {Task, Tasks, User, Users} from '../Model';
import RC2 from 'react-chartjs2';
import {isNullOrUndefined, isUndefined} from 'util';
import L = require('lodash');
import {randomColor} from "./Common";

export interface TasksChartProps {
    tasks: Tasks;
    users: Users;
}

interface UserModel {
    user: User;
    taskPoints: TaskModel[];
}

interface TaskModel {
    task: Task;
    point: {
        x: number;
        y: number;
    };
}

export const TaskChart: React.StatelessComponent<TasksChartProps> = ({tasks, users}) => {
        if (isUndefined(users.users) || isUndefined((tasks.tasks))) {
            return (<div>Loading...</div>);
        }
        const completedTasks: Task[] = L.sortBy(tasks.tasks.filter(it => it.completedAt), t => t.completedAt);
        const usersTasks = function (userId: number): Task[] {
            return completedTasks.filter(it => it.userId == userId);
        };
        const pointsUntilTask = function (task: Task): number {
            const allTasksForUser = usersTasks(task.userId);
            const olderThan = L.filter(allTasksForUser, it => it.completedAt < task.completedAt);
            return task.chore.points + L.sumBy(olderThan, it => it.chore.points)
        };
        const chartModel: UserModel[] = users.users.map(user => ({
                user: user,
                taskPoints: usersTasks(user.id).map(task => ({
                    task: task,
                    point: {
                        x: task.completedAt,
                        y: pointsUntilTask(task)
                    }
                }))
            })
        );

        const chartData = {
            datasets: chartModel.map(userModel => {
                const color = randomColor();
                return {
                    borderColor: color,
                    backgroundColor: color,
                    fill: false,
                    label: userModel.user.name,
                    data: userModel.taskPoints.map(it => it.point),
                    lineTension: 0
                }
            })
        };

        //dataSetIndex
        //index
        const chartOptions = {
            title: {
                display: true,
                text: 'Tasks'
            },
            elements: {
                point: {
                    radius: 5,
                    hoverRadius: 7,
                }
            },
            hover: {
                mode: 'x',
                intersect: false
            },
            tooltips: {
                mode: 'x',
                intersect: false,
                title: {
                    show: false
                },
                callbacks: {
                    title: function (items) {
                        const usersModels: UserModel[] = items.map(it => chartModel[it.datasetIndex]);
                        const tasksModels: TaskModel[] = items.map(it => chartModel[it.datasetIndex].taskPoints[it.index]);
                        const uniqueUsers: UserModel[] = L.uniq(usersModels);
                        const date = new Date(tasksModels[0].task.completedAt);
                        const time = date.toLocaleString('pl-PL');
                        return [time].concat(uniqueUsers.map(userModel => {
                                const userTasks = userModel.taskPoints.filter(it => L.includes(tasksModels, it));
                                const newestTask: TaskModel = L.maxBy(userTasks, t => t.point.y);
                                const totalPoints = newestTask.point.y;
                                const userName = userModel.user.name;
                                return userName + ' (' + totalPoints + ' points)';
                            }
                        ));
                    },
                    label: function (item) {
                        const taskPoint = chartModel[item.datasetIndex].taskPoints[item.index];
                        const taskName = taskPoint.task.chore.name;
                        const taskPoints = taskPoint.task.chore.points;
                        const totalPoints = taskPoint.point.y;
                        return taskName + ' +' + taskPoints + ' points';
                    }
                }
            },
            scales: {
                xAxes: [{
                    display: true,
                    ticks: {
                        callback: (value) => {
                            return new Date(value).toLocaleDateString('pl-PL');
                        }
                    }
                }]
            }
        };
        return (<RC2 data={chartData} options={chartOptions} type="scatter"/>);
    }
;