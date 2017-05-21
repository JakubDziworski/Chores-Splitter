import * as React from "react";
import {Task, Tasks} from "../Model";
import RC2 from "react-chartjs2";
import {isUndefined} from "util";
import L = require('lodash');
import {randomColor} from "./Common";

export interface ChoresPopularityProps {
    tasks: Tasks;
}

interface ChoreAmount {
    choreName: string;
    amount: number;
}

export const ChoresPopularity: React.StatelessComponent<ChoresPopularityProps> = ({tasks}) => {

        if (isUndefined((tasks.tasks))) {
            return (<div>Loading...</div>);
        }
        const tasksList = tasks.tasks.filter(it => it.completed);
        const choresAmounts: ChoreAmount[] = L.toArray(L.groupBy(tasksList.map(it => it.chore.name), it => it))
            .map(it => {
                return {
                    choreName: it[0],
                    amount: it.length
                }
            });


        const chartData = {
            datasets: [{
                data: choresAmounts.map(it => it.amount),
                backgroundColor: choresAmounts.map(it => randomColor()),
                label: 'Dataset 1'
            }],
            labels: choresAmounts.map(it => it.choreName)
        };

        const chartOptions = {
            responsive:true,
            legend: {
                position: 'left',
            },
            title: {
                display: true,
                text: 'Chores popularity'
            }
        };

        return (<RC2 data={chartData} options={chartOptions} type="doughnut"/>);
    }
;