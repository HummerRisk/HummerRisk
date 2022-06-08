import {CUR_YEAR, UPPER_LIMIT_YEAR} from '../constant/filed'

export default {
  common: {
    from: 'From',
    fromThe: 'From',
    start: ' Start ',
    every: 'Per ',
    between: '',
    and: ' To ',
    end: ' ',
    specified: 'Specified',
    symbolTip: 'Symbol ',
    valTip: 'Value ',
    nearest: ' Nearest',
    current: 'Current ',
    nth: ' The',
    index: 'th ',
    placeholder: 'Select',
    placeholderMulti: '(Multi)Select',
    help: 'Help',
    wordNumError: 'Need 6 or 7 words',
    reverse: 'Reverse',
    reset: 'Reset',
    tagError: 'Tag Error ',
    numError: 'Number Error ',
    use: 'Use',
    inputPlaceholder: 'Cron Expression'
  },
  custom: {
    unspecified: 'Unspecified',
    workDay: ' Work Day',
    lastTh: ' Last',
    lastOne: ' Last',
    latestWorkday: ' Last Work Day',
    empty: 'Empty'
  },
  second: {
    title: 'Second',
    val: '0 1 2...59'
  },
  minute: {
    title: 'Minute',
    val: '0 1 2...59'
  },
  hour: {
    title: 'Hour',
    val: '0 1 2...23'
  },
  dayOfMonth: {
    timeUnit: 'Day',
    title: 'Day',
    val: '1 2...31'
  },
  month: {
    title: 'Month',
    val: '1 2...12 or JAN ... DEC'
  },
  dayOfWeek: {
    timeUnit: 'Day',
    title: 'Week',
    val: '1 2...7 or SUN ... SAT',
    SUN: 'Sunday',
    MON: 'Monday',
    TUE: 'Tuesday',
    WED: 'Wednesday',
    THU: 'Thursday',
    FRI: 'Friday',
    SAT: 'Saturday'
  },
  year: {
    title: 'Year',
    val: CUR_YEAR + ' ... ' + UPPER_LIMIT_YEAR
  },
  period: {
    startError: 'Start is Error',
    cycleError: 'Cycle is Error'
  },
  range: {
    lowerError: 'Lower is Error',
    upperError: 'Upper is Error',
    lowerBiggerThanUpperError: 'Lower should smaller than Upper'
  },
  weekDay: {
    weekDayNumError: 'The Week Number is Error',
    nthError: 'The Day in Week is Error'
  },
  app: {
    title: 'Cron Generator Implemented by Vue.js and Element-ui'
  }
}
