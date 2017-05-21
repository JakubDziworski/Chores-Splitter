import * as React from 'react';
import {Task, Tasks, Users} from '../Model';
import RC2 from 'react-chartjs2';
import {isUndefined} from 'util';
import L = require('lodash');

export interface TasksChartProps {
    tasks: Tasks;
    users: Users;
}

export const TaskChart: React.StatelessComponent<TasksChartProps> = ({tasks, users}) => {
        const randomColor = () => {
            const letters = '0123456789ABCDEF';
            let color = '#';
            for (let i = 0; i < 6; i++) {
                color += letters[Math.floor(Math.random() * 16)];
            }
            return color;
        };

        if (isUndefined(users.users) || isUndefined((tasks.tasks))) {
            return (<div>Loading...</div>);
        }
        const tasksList = tasks.tasks.filter(it => it.completed).reverse();
        const usersTasks = (userId: number) => tasksList.filter(it => it.userId == userId);

        function pointsUntilTask(task: Task): number {
            const allTasksForUser = usersTasks(task.userId);
            const olderThan = L.filter(allTasksForUser, it => it.id < task.id);
            return task.chore.points + L.sumBy(olderThan, it => it.chore.points)
        }
    const chartData = {
            datasets: users.users.map(user => {
                const color = randomColor();
                return {
                    borderColor: color,
                    backgroundColor: color,
                    fill: false,
                    label: user.name,
                    data: usersTasks(user.id).map(it => (
                            {
                                x: it.id,
                                y: pointsUntilTask(it)
                            }
                        )
                    ),
                    lineTension: 0
                }
            })
        };
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
            tooltips : {
                title : {
                    show : false
                },
                callbacks : {
                    title : function (items) {
                        const item = items[0];
                        const task = tasksList.find(it => it.id == item.xLabel);
                        const totalPoints = item.yLabel;
                        const userName = users.users.find(it => it.id == task.userId).name;
                        const time = new Date(task.assignedAt).toLocaleString('pl-PL');
                        return userName + '(' + totalPoints + ' points) - ' + time ;
                    },
                    label : function (item) {
                        const task = tasksList.find(it => it.id == item.xLabel);
                        const taskName = task.chore.name;
                        const taskPoints = task.chore.points;
                        return taskName + '(+' + taskPoints +' points)';
                    }
                }
            },
            scales: {
                xAxes: [{
                    display: false,
                }]
            }
        };
        return (<RC2 data={chartData} options={chartOptions} type="scatter"/>);
    }
;